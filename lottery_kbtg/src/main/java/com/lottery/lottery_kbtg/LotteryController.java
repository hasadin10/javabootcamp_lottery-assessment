package com.lottery.lottery_kbtg;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LotteryController {
    public List<Lottery> lotteries = new ArrayList<>(
            List.of(
                    new Lottery("4835700311" , "100" , 1 , "1"),
                    new Lottery("9541633103" , "100" , 1 , "2")
            )
    );

    //Api for get all ticket
    @GetMapping("/all_lotteries")
    public List<Lottery> getLotteries() {
        return lotteries;
    }

    //Story: EXP01
    @PostMapping("/admin/lotteries")
    public String addLottery(@RequestBody Lottery request) {
        String ticket = request.getTicket();
        String price = request.getPrice();
        int amount = request.getAmount();

        for (Lottery lottery : lotteries) {
            if (lottery.getTicket().equals(ticket)) {
                lottery.setAmount(lottery.getAmount() + amount);
                return "Amount incremented for ticket " + ticket;
            }
        }
        lotteries.add(new Lottery(ticket, price, amount, null));
        return "Lottery added successfully";
    }

    // From Story: EXP01 after you use api /admin/lotteries for add new ticket you have yo use /admin/add/lotteries/{ticketId}/users/{userId} for update userId owner for that ticket.
    @PostMapping("/admin/add/lotteries/{ticketId}/users/{userId}")
    public String addLotteryByAdmin(
            @PathVariable String userId,
            @PathVariable String ticketId
    ) {
        System.out.println("Here.");
        // Check if the ticket exists
        for (Lottery lottery : lotteries) {
            System.out.println("lottery" + lottery);
            if (lottery.getTicket().equals(ticketId)) {
                if (lottery.getUser_id() == null) {
                    lottery.setUser_id(userId);
                    return "User ID added to ticket " + ticketId;
                } else {
                    return "User ID already exists for ticket " + ticketId;
                }
            }
        }

        // If the ticket does not exist, add a new Lottery object to the list
        lotteries.add(new Lottery(ticketId,"100", 1, userId));
        return "Lottery added successfully";
    }

    //Story: EXP02
    @GetMapping("/lotteries")
    public Map<String, List<String>> getLottery(){
        List<String> tickets = new ArrayList<>();
        for (Lottery lottery : lotteries) {
            tickets.add(lottery.getTicket());
        }

        Map<String, List<String>> result = new HashMap<>();
        result.put("ticket", tickets);
        return result;
    }
    //Story: EXP03
    @PostMapping("/users/{userId}/lotteries/{ticketId}")
    public String addLottery(@PathVariable String userId, @PathVariable String ticketId) {

        for (Lottery lottery : lotteries) {
            if (lottery.getTicket().equals(ticketId) && !lottery.getUser_id().equals(userId)) {
                return "Cannot add the ticket. It already exists with a different user.";
            }
        }

        for (Lottery lottery : lotteries) {
            if (lottery.getTicket().equals(ticketId)) {
                // If the ticket already exists, increment the amount and return
                lottery.setAmount(lottery.getAmount() + 1);
                return "Amount incremented for ticket " + ticketId;
            }
        }

        // If the ticket does not exist, add a new Lottery object to the list
        lotteries.add(new Lottery(ticketId, "100", 1, userId));
        return "Lottery added successfully";
    }

    //Story: EXP04
    @GetMapping("/user/{userId}/lotteries")
    public List<String> getLotteriesByUserId(@PathVariable String userId) {
        return lotteries.stream()
                .filter(lottery -> lottery.getUser_id().equals(userId))
                .map(Lottery::getTicket)
                .collect(Collectors.toList());
    }

    //Story: EXP05
    @DeleteMapping("/users/{userId}/lotteries/{ticketId}")
    public String deleteLottery(@PathVariable String userId, @PathVariable String ticketId) {
        Iterator<Lottery> iterator = lotteries.iterator();
        while (iterator.hasNext()) {
            Lottery lottery = iterator.next();
            if (lottery.getUser_id().equals(userId) && lottery.getTicket().equals(ticketId)) {
                iterator.remove();
                return "Lottery with ticket ID " + ticketId + " deleted successfully";
            }
        }
        return "Lottery with ticket ID " + ticketId + " not found for user ID " + userId;
    }
}

class Lottery {

    private String ticket;

    private String price;

    private int amount;

    private String user_id;

    public Lottery(String ticket, String price, Integer amount, String user_id) {
        this.ticket = ticket;
        this.price = price;
        this.amount = amount;
        this.user_id = user_id;
    }

    public String getTicket() {
        return ticket;
    }

    public String getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

