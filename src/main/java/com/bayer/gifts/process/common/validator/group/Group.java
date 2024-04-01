package com.bayer.gifts.process.common.validator.group;

import javax.validation.GroupSequence;

/**
 * 定义校验顺序，如果AddGroup组失败，则UpdateGroup组不会再校验
 *
 * @author wangym
 */
@GroupSequence({AddGroup.class, UpdateGroup.class,QueryGroup.class})
public interface Group {

}
