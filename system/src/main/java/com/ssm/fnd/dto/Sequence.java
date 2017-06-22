package com.ssm.fnd.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.mybatis.annotation.Condition;
import com.ssm.mybatis.annotation.ExtensionAttribute;
import com.ssm.sys.dto.BaseDTO;
/**
 * @name        Sequence
 * @description 序列号
 * @author      meixl
 * @date        2017年6月20日上午11:49:49
 * @version
 */
@ExtensionAttribute(disable = true)
@Table(name = "tb_fnd_sequence")
public class Sequence extends BaseDTO {
	
	/**
	 */
	private static final long serialVersionUID = 4114631733744945966L;

	@Id
	@GeneratedValue
	private Long sequenceId;

	@NotEmpty
	private String sequenceCode;

	@NotEmpty
	@Condition(operator = "LIKE", autoWrap = false)
	private String sequenceName;

	private String sequencePrefix;

	private String dateFormat;

	private String incrCode;

	@NotEmpty
	private String seqLength;
	
	private Long serialNumber;

	public void setSequenceId(Long sequenceId) {
		this.sequenceId = sequenceId;
	}

	public Long getSequenceId() {
		return sequenceId;
	}

	public void setSequenceCode(String sequenceCode) {
		this.sequenceCode = sequenceCode;
	}

	public String getSequenceCode() {
		return sequenceCode;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequencePrefix(String sequencePrefix) {
		this.sequencePrefix = sequencePrefix;
	}

	public String getSequencePrefix() {
		return sequencePrefix;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public String getIncrCode() {
		return incrCode;
	}

	public void setIncrCode(String incrCode) {
		this.incrCode = incrCode;
	}

	public void setSeqLength(String seqLength) {
		this.seqLength = seqLength;
	}

	public String getSeqLength() {
		return seqLength;
	}

	public Long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Long serialNumber) {
		this.serialNumber = serialNumber;
	}
	
}