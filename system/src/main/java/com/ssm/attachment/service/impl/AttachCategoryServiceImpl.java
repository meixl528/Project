package com.ssm.attachment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.attachment.dto.AttachCategory;
import com.ssm.attachment.exception.StoragePathNotExsitException;
import com.ssm.attachment.mapper.AttachCategoryMapper;
import com.ssm.attachment.service.IAttachCategoryService;
import com.ssm.core.BaseConstants;
import com.ssm.core.annotation.StdWho;
import com.ssm.core.request.IRequest;

/**
 * 附件类型service.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttachCategoryServiceImpl implements IAttachCategoryService {

    /**
     * 找不到对应的路径.
     **/
    public static final String CATEGORY_PATH_NOT_CORRECT = "category_path_not_correct";

    @Autowired
    private AttachCategoryMapper categoryMapper;

    @Override
    public AttachCategory insert(IRequest requestContext, @StdWho AttachCategory category) {
        if (StringUtils.isBlank(category.getSourceType())) {
            category.setSourceType(AttachCategory.DEFAULT_SOURCETYPE);
        }
        if (StringUtils.isBlank(category.getIsUnique())) {
            category.setIsUnique(BaseConstants.NO);
        }
        if (StringUtils.isBlank(category.getStatus())) {
            category.setStatus(AttachCategory.STATUS_NORMAL);
        }
        // 更新其父节点不为空
        AttachCategory parent = categoryMapper.selectByPrimaryKey(category.getParentCategoryId());
        if (parent != null && AttachCategory.LEAF_TRUE.equals(parent.getLeafFlag())) {
            parent.setLeafFlag(AttachCategory.LEAF_FALSE);
            categoryMapper.updateByPrimaryKeySelective(parent);
        }
        categoryMapper.insertSelective(category);
        category.setPath(String.valueOf(category.getCategoryId()));
        if (category.getParentCategoryId() != null) {
            AttachCategory p1 = new AttachCategory();
            p1.setCategoryId(category.getParentCategoryId());
            p1 = categoryMapper.selectByPrimaryKey(p1);
            if (p1 != null) {
                category.setPath(p1.getPath() + "." + category.getCategoryId());
            }
        }
        // update path
        categoryMapper.updateByPrimaryKeySelective(category);
        return category;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public AttachCategory selectAttachByCode(IRequest requestContext, String sourceCode) {
        if (StringUtils.isBlank(sourceCode)) {
            return null;
        }
        AttachCategory params = new AttachCategory();
        params.setSourceType(sourceCode);
        params.setStatus(AttachCategory.STATUS_NORMAL);
        return categoryMapper.selectCategory(params);
    }

    /*
     * 根据sourceType 查找绝对路径 比如：/home/attachment/image/2015/1
     * 
     * @see IAttachCategoryService#selectPathByCode( IRequest, java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public String selectPathByCode(IRequest requestContext, String sourceType) throws StoragePathNotExsitException {
        AttachCategory params = new AttachCategory();
        params.setSourceType(sourceType);
        AttachCategory category = categoryMapper.selectCategory(params);

        if (category == null || category.getCategoryPath().length() <= 0) {
            throw new StoragePathNotExsitException();
        }
        return category.getCategoryPath();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AttachCategory> selectCategories(AttachCategory category) {
        return categoryMapper.select(category);
    }

    @Override
    public List<AttachCategory> batchUpdate(IRequest requestCtx, @StdWho List<AttachCategory> categories) {
        for (AttachCategory category : categories) {

            if (category.getCategoryId() == null) {
                self().insert(requestCtx, category);
            } else {
                categoryMapper.updateByPrimaryKeySelective(category);
            }
        }
        return categories;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public AttachCategory selectByPrimaryKey(IRequest requestContext, Long categoryId) {
        return categoryMapper.selectByPrimaryKey(categoryId);
    }

    @Override
    public boolean remove(AttachCategory category) {
        category = categoryMapper.selectByPrimaryKey(category.getCategoryId());
        if (category == null || AttachCategory.LEAF_FALSE.equals(category.getLeafFlag())) {
            return false;
        } else {
            category.setStatus(AttachCategory.STATUS_DELETED);
            categoryMapper.updateByPrimaryKeySelective(category);
            return true;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AttachCategory> selectCategories(IRequest requestContext, AttachCategory category) {
        return categoryMapper.select(category);
    }

    @Override
    public AttachCategory validateType(List<AttachCategory> categories) {
        List<String> types = categoryMapper.selectAllSourceTypes();
        for (AttachCategory category : categories) {
            if (category.getCategoryId() == null && types.contains(category.getSourceType())) {
                return category;
            }
        }
        return null;
    }

    public List<AttachCategory> selectCategoryBreadcrumbList(IRequest requestContext, Long categoryId) {

        AttachCategory a1 = new AttachCategory();
        a1.setCategoryId(categoryId);

        AttachCategory current = categoryMapper.selectByPrimaryKey(a1);

        String path = "";
        if (current != null) {
            path = StringUtils.trimToEmpty(current.getPath());
        }
        List<Long> idList = new ArrayList<>();
        idList.add(-1L);
        String[] paths = StringUtils.split(path, ".");
        for (int i = 0; i < paths.length; i++) {
            idList.add(Long.parseLong(paths[i]));
        }

        List<AttachCategory> cates = categoryMapper.selectAllParentCategory(idList);
        if (cates != null && !cates.isEmpty()) {
            cates.sort((a, b) -> {
                if (a.getCategoryId().equals(b.getParentCategoryId())) {
                    return -1;
                }
                if (a.getParentCategoryId().equals(b.getCategoryId())) {
                    return 1;
                }
                return 0;
            });
            cates.forEach(a -> {
                a.setCategoryName(StringUtils.abbreviate(a.getCategoryName(), 15));
            });
        }
        return cates;
    }

    @Override
    public List<AttachCategory> queryTree(IRequest requestContext, AttachCategory category) {
        List<AttachCategory> list = categoryMapper.queryTree(category);
        for (AttachCategory attachCategory : list) {
            if (Objects.equals(attachCategory.getParentCategoryId(), -1L)) {
                attachCategory.setParentCategoryId(null);
            }
        }
        return list;
    }

}
