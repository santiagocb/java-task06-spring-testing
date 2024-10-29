package com.ticketland.controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.ticketland.entities.Ticket;
import com.ticketland.entities.UserAccount;
import com.ticketland.facades.BookingFacade;
import com.ticketland.oxm.TicketsXML;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class TicketController {

    public static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    private final Jaxb2Marshaller jaxb2Marshaller;

    private final BookingFacade bookingFacade;

    public TicketController(BookingFacade bookingFacade, Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
        this.bookingFacade = bookingFacade;
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getTickets() {
        return ResponseEntity.ok(bookingFacade.getAllTickets());
    }


    @GetMapping("/tickets/user")
    public ResponseEntity<Object> getBookedTickets(@RequestParam(value = "userId") String userId) {
        try {
            UserAccount user = bookingFacade.getUserAccount(userId);
            List<Ticket> tickets = bookingFacade.getTicketsByUserAccountId(user.getId());
            return ResponseEntity.ok(tickets);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @GetMapping(value = "/tickets/user", params = "downloadPdf=true")
    public ResponseEntity<?> downloadTickets(@RequestParam(value = "userId") String userId) {
        UserAccount user = bookingFacade.getUserAccount(userId);
        List<Ticket> tickets = bookingFacade.getBookedTickets(user, 100, 1);;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tickets.pdf");
        return new ResponseEntity<>(generateTicketsPdf(tickets, user), headers, HttpStatus.OK);
    }

    @PostMapping("/tickets/upload")
    public ResponseEntity<?> uploadTickets(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File empty");

        try {
            File tempFile = File.createTempFile("tickets", ".xml");
            file.transferTo(tempFile);

            bookingFacade.preloadTickets(loadTicketsFile(tempFile.getAbsolutePath()));
            tempFile.deleteOnExit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    private List<Ticket> loadTicketsFile(String filePath) {
        File xmlFile = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(xmlFile)) {
            TicketsXML ticketsXML = (TicketsXML) jaxb2Marshaller.unmarshal(new StreamSource(fileInputStream));

            return ticketsXML.getTickets().stream().map(tx -> {
                var user = bookingFacade.getUserAccount(tx.getUser());
                var event = bookingFacade.getEventById(tx.getEvent());
                return new Ticket(user, event);
            }).toList();
        } catch (IOException e) {
            logger.error("Failed to read the XML file: " + e.getMessage(), e);
            throw new RuntimeException("Failed to read the XML file: " + e.getMessage(), e);
        }
    }

    private byte[] generateTicketsPdf(List<Ticket> tickets, UserAccount user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Booked Tickets for: " + user.getUser().getName())
                    .setBold()
                    .setFontSize(18)
                    .setMarginBottom(10));

            Table table = new Table(4);
            table.addCell(new Cell().add(new Paragraph("Event ID")).setBold());
            table.addCell(new Cell().add(new Paragraph("Event Name")).setBold());
            table.addCell(new Cell().add(new Paragraph("Place")).setBold());
            table.addCell(new Cell().add(new Paragraph("Date")).setBold());

            for (Ticket ticket : tickets) {
                table.addCell(new Cell().add(new Paragraph(ticket.getEvent().getId())));
                table.addCell(new Cell().add(new Paragraph(ticket.getEvent().getName())));
                table.addCell(new Cell().add(new Paragraph(ticket.getEvent().getPlace())));
                table.addCell(new Cell().add(new Paragraph(ticket.getEvent().getDate().toString())));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
