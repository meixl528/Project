package com.ssm.attachment.service;

import java.util.List;

import com.ssm.attachment.dto.Attachment;
import com.ssm.attachment.dto.SysFile;
import com.ssm.attachment.exception.UniqueFileMutiException;
import com.ssm.core.annotation.StdWho;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;

/**
 * 文件接口.
 */
public interface ISysFileService extends ProxySelf<ISysFileService> {

    /**
     * 插入.
     * 
     * @param requestContext
     *            IRequest
     * @param file
     *            SysFile对象
     * @return SysFile SysFile对象
     */
    SysFile insert(IRequest requestContext, SysFile file);

    /**
     * 删除文件.
     * 
     * @param requestContext
     *            IRequest
     * @param file
     *            SysFile对象
     * @return SysFile SysFile对象
     */
    SysFile delete(IRequest requestContext, SysFile file);

    /**
     * 返回文件列表.
     * 
     * @param requestContext
     *            IRequest
     * @param file
     *            SysFile
     * @param page
     *            页数
     * @param pagesize
     *            单页条数
     * @return List SysFile列表
     */
    List<SysFile> selectFiles(IRequest requestContext, SysFile file, int page, int pagesize);

    /**
     * 根据参数和分类Id返回文件列表.
     * 
     * @param requestContext
     *            IRequest
     * @param file
     *            SysFile参数
     * @param categoryId
     *            分类主健id
     * @param page
     *            页数
     * @param pagesize
     *            单页条数
     * @return List SysFile列表
     */
    List<SysFile> selectFilesByCategoryId(IRequest requestContext, SysFile file, Long categoryId, int page,
            int pagesize);

    /**
     * 文件和附件一同保存.
     * 
     * @param requestContext
     *            IRequest
     * @param attach
     *            Attachment
     * @param file
     *            SysFile
     * @return SysFile 文件对象
     */
    SysFile insertFileAndAttach(IRequest requestContext, @StdWho Attachment attach, @StdWho SysFile file);

    /**
     * 
     * 对于分类唯一的来调用.
     * 
     * @param requestContext
     *            IRequest
     * @param attach
     *            Attachment对象
     * @param file
     *            SysFile对象
     * @return SysFile 返回更新或者插入的SysFile对象
     * @throws UniqueFileMutiException
     *             此分类下有多个附件 异常
     */
    SysFile updateOrInsertFile(IRequest requestContext, @StdWho Attachment attach, @StdWho SysFile file)
            throws UniqueFileMutiException;

    /**
     * 根据id找到这个文件.
     * 
     * @param requestContext
     *            IRequest
     * @param fileId
     *            文件对象id
     * @return SysFile 文件对象
     */
    SysFile selectByPrimaryKey(IRequest requestContext, Long fileId);

    /**
     * 根据sourceType 和sourceKey 查找文件列表.
     *
     * @param requestContext
     *            IRequest
     * @param sourceType
     *            业务表code
     * @param sourceKey
     *            业务主健id
     * @return List SysFile列表
     *
     *
     */

    List<SysFile> selectFilesByTypeAndKey(IRequest requestContext, String sourceType, String sourceKey);

    List<SysFile> selectFilesByTypeAndKey(IRequest requestContext, String sourceType, Long sourceKey);

    /**
     * 批量删除文件.
     * 
     * @param requestContext
     *            IRequest
     * @param sysFiles
     *            SysFile类型的List集合
     */
    void removeFiles(IRequest requestContext, List<SysFile> sysFiles);

    /**
     * 根据id列表查找文件对象.
     * 
     * @param requestContext
     *            IRequest
     * @param fileIds
     *            String类型List
     * @return List SysFile类型List
     */
    List<SysFile> selectByIds(IRequest requestContext, List<String> fileIds);

    /**
     * 删除图片.
     * 
     * @param requestContext
     *            IRequest
     * @param file
     *            SysFile对象
     * @return SysFile SysFile对象
     */

    SysFile deleteImage(IRequest requestContext, SysFile file);

    /* 删除上传文件*/
    void deletefiles(IRequest requestContext, SysFile file);
    /* 通过sourcetyp和sourcekey查询数据 */
    List<SysFile> queryFilesByTypeAndKey(IRequest requestContext, String sourceType, String sourceKey);
}
