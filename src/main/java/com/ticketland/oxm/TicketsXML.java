package com.ticketland.oxm;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "tickets")
public class TicketsXML {

    private List<TicketXML> tickets;

    @XmlElement(name = "ticket")
    public List<TicketXML> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketXML> tickets) {
        this.tickets = tickets;
    }
}