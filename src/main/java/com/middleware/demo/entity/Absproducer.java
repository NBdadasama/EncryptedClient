package com.middleware.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName absproducer
 */
@TableName(value ="absproducer")
@Data
public class Absproducer implements Serializable {
    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String exchange;

    /**
     * 
     */
    private String routingkey;

    /**
     * 
     */
    private String consumername;

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
        Absproducer other = (Absproducer) that;
        return (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getExchange() == null ? other.getExchange() == null : this.getExchange().equals(other.getExchange()))
            && (this.getRoutingkey() == null ? other.getRoutingkey() == null : this.getRoutingkey().equals(other.getRoutingkey()))
            && (this.getConsumername() == null ? other.getConsumername() == null : this.getConsumername().equals(other.getConsumername()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getExchange() == null) ? 0 : getExchange().hashCode());
        result = prime * result + ((getRoutingkey() == null) ? 0 : getRoutingkey().hashCode());
        result = prime * result + ((getConsumername() == null) ? 0 : getConsumername().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", username=").append(username);
        sb.append(", exchange=").append(exchange);
        sb.append(", routingkey=").append(routingkey);
        sb.append(", consumername=").append(consumername);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}