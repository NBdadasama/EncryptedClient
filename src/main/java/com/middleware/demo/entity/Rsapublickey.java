package com.middleware.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @TableName rsapublickey
 */
@TableName(value ="rsapublickey")
@Data
public class Rsapublickey implements Serializable {
    /**
     * 
     */
    @TableId
    private String username;

    /**
     * 
     */
    private byte[] rsapublickey;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Rsapublickey other = (Rsapublickey) that;
        return (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (Arrays.equals(this.getRsapublickey(), other.getRsapublickey()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + (Arrays.hashCode(getRsapublickey()));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", username=").append(username);
        sb.append(", rsapublickey=").append(rsapublickey);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}