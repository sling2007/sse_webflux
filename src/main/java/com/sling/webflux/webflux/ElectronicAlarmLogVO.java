package com.sling.webflux.webflux;


/**
 * Created by IntelliJ IDEA.
 *
 * @author zac
 * @Date 2021/9/16  10:48
 * @description
 */
//@Data
public class ElectronicAlarmLogVO {

//    @ApiModelProperty("id")
    private String id;

//    @ApiModelProperty("告警设备名称")
    private String name;

//	@ApiModelProperty("告警标题")
	private String title;

//	@ApiModelProperty("发生时间")
	private String occurTime;

//	@ApiModelProperty("告警级别 1:严重  2:重要 3:疑似 4:紧急 5:一般 6:提示")
	private Integer alarmLevel;

//	@ApiModelProperty("告警来源 NCE,IVS")
	private String source;

//	@ApiModelProperty("防区段id")
	private String defenseSectionId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOccurTime() {
		return occurTime;
	}

	public void setOccurTime(String occurTime) {
		this.occurTime = occurTime;
	}

	public Integer getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(Integer alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDefenseSectionId() {
		return defenseSectionId;
	}

	public void setDefenseSectionId(String defenseSectionId) {
		this.defenseSectionId = defenseSectionId;
	}
}
