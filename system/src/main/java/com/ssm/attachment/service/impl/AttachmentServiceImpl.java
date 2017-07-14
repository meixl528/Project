package com.ssm.attachment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.attachment.dto.Attachment;
import com.ssm.attachment.dto.SysFile;
import com.ssm.attachment.mapper.AttachmentMapper;
import com.ssm.attachment.mapper.SysFileMapper;
import com.ssm.attachment.service.IAttachmentService;
import com.ssm.attachment.service.ISysFileService;
import com.ssm.core.annotation.StdWho;
import com.ssm.core.request.IRequest;

/**
 * 附件service.
 */
@Service
@Transactional
public class AttachmentServiceImpl implements IAttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;
    
    @Autowired
    private SysFileMapper sysFileMapper;
    
    @Autowired
    private ISysFileService sysFileService;
    
    

    @Override
    public Attachment insert(IRequest requestContext, @StdWho Attachment attach) {
        attachmentMapper.insertSelective(attach);
        return attach;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Attachment selectAttachByCodeAndKey(IRequest requestContext, String sourceType, String sourceKey) {
        Attachment attachment = new Attachment();
        attachment.setSourceKey(sourceKey);
        attachment.setSourceType(sourceType);
        return attachmentMapper.selectAttachment(attachment);
    }

    @Override
    public Attachment deleteAttachment(IRequest requestContext, Attachment attach) {
        
        SysFile file = new SysFile();
        file.setAttachmentId(attach.getAttachmentId());
        List<SysFile> files = sysFileMapper.select(file);
        for (SysFile f : files) {
            sysFileService.delete(requestContext, f);
        }
        attachmentMapper.delete(attach);
        return attach;
    }

}
