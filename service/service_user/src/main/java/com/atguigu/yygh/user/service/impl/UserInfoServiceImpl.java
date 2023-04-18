package com.atguigu.yygh.user.service.impl;

import com.atguigu.enums.AuthStatusEnum;
import com.atguigu.model.user.Patient;
import com.atguigu.model.user.UserInfo;
import com.atguigu.vo.user.LoginVo;
import com.atguigu.vo.user.UserInfoQueryVo;
import com.atguigu.yygh.exception.YyghException;
import com.atguigu.yygh.user.helper.JwtHelper;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PatientService patientService;


    private Map<String, Object> phoneLogin(LoginVo loginVo) {
        //手机号+验证码 校验参数
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "手机号或验证码为空");
        }

        //2、校验校验验证码
        String codeFromRedis = stringRedisTemplate.opsForValue().get(phone);

        if (StringUtils.isEmpty(codeFromRedis)) {
            throw new YyghException(20001, "验证码已过期");
        }

        if (!code.equals(codeFromRedis)) {
            throw new YyghException(20001, "验证码错误");
        }
        //手机号+验证码登录逻辑
        //3、检查手机号是否已被使用
        UserInfo userInfo = userInfoMapper
                .selectOne(new QueryWrapper<UserInfo>()
                        .eq("phone", phone));

        if (null == userInfo) {
            //4、如果手机号未被使用，根据手机号创建用户
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setName(phone);
            userInfo.setAuthStatus(AuthStatusEnum.NO_AUTH.getStatus());
            userInfo.setStatus(1);
            userInfo.setCreateTime(new Date());
            userInfo.setUpdateTime(new Date());
            int insert = userInfoMapper.insert(userInfo);
            if (insert <= 0) {
                throw new YyghException(20001, "用户创建失败");
            }
        }

        //校验是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YyghException(20001, "用户被禁用");
        }

        //返回页面name和token
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        map.put("name", name);
        return map;
    }

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        return null;
    }

    @Override
    public Page<UserInfo> selectPage(Long page, Long limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);

        String keyword = userInfoQueryVo.getKeyword();

        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        Integer status = userInfoQueryVo.getStatus();

        Integer authStatus = userInfoQueryVo.getAuthStatus();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("name", keyword).or().like("phone", keyword);
        }


        if (!StringUtils.isEmpty(createTimeBegin)) {
            queryWrapper.ge("create_time", createTimeBegin);
        }


        if (!StringUtils.isEmpty(createTimeEnd)) {
            queryWrapper.le("create_time", createTimeEnd);
        }

        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }

        if (!StringUtils.isEmpty(authStatus)) {
            queryWrapper.eq("auth_status", authStatus);
        }


        baseMapper.selectPage(pageParam, queryWrapper);

        List<UserInfo> records = pageParam.getRecords();
        records.forEach(this::packUserInfo);
        return pageParam;
    }

    private void packUserInfo(UserInfo userInfo) {

        Integer status = userInfo.getStatus();
        Integer authStatus = userInfo.getAuthStatus();
        userInfo.getParam().put("statusString", status == 1 ? "可用" : "不可用");
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(authStatus));
    }

    @Override
    public void lock(Long userId, Integer status) {
        if (status == 0 || status == 1) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        } else {
            throw new YyghException(20001, "状态值不正确");
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> map = new HashMap<>();

        //根据userid查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        this.packUserInfo(userInfo);

        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);

        map.put("userInfo", userInfo);
        map.put("patientList", patientList);

        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus == 1 || authStatus == -1) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        } else {
            throw new YyghException(20001, "状态值不正确");
        }
    }


}

