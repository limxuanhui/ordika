package io.bluextech.ordika.models;
/* Created by limxuanhui on 30/11/23 */

public class Asset {

    private String base64;
    private String uri;
    private Integer width;
    private Integer height;
    private Long fileSize;
    private String type;
    private String fileName;
    private Long duration;
    private Integer bitrate;
    private String timestamp;
    private String id;

    public Asset() {
    }

    public Asset(String base64, String uri, Integer width, Integer height, Long fileSize, String type, String fileName, Long duration, Integer bitrate, String timestamp, String id) {
        this.base64 = base64;
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.fileSize = fileSize;
        this.type = type;
        this.fileName = fileName;
        this.duration = duration;
        this.bitrate = bitrate;
        this.timestamp = timestamp;
        this.id = id;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "base64='" + base64 + '\'' +
                ", uri='" + uri + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", fileSize=" + fileSize +
                ", type='" + type + '\'' +
                ", fileName='" + fileName + '\'' +
                ", duration=" + duration +
                ", bitrate=" + bitrate +
                ", timestamp='" + timestamp + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
