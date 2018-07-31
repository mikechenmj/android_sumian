package com.sumian.hw.network.response;

/**
 * Created by jzz
 * on 2017/10/19.
 * desc:
 */

public class Ticket {
    private String ticket;

    public String getTicket() {
        return ticket;
    }

    public Ticket setTicket(String ticket) {
        this.ticket = ticket;
        return this;
    }

    @Override
    public String toString() {
        return "Ticket{" +
            "ticket='" + ticket + '\'' +
            '}';
    }
}
