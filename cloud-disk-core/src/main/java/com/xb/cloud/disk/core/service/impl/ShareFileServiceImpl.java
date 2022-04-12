package com.xb.cloud.disk.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.cloud.disk.core.entity.ShareFile;
import com.xb.cloud.disk.core.mapper.ShareFileMapper;
import com.xb.cloud.disk.core.service.ShareFileService;
import org.springframework.stereotype.Service;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
@Service
public class ShareFileServiceImpl extends ServiceImpl<ShareFileMapper, ShareFile>
    implements ShareFileService {}
