package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        // your code here
        // create a new partner with given partnerId and save it
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, deliveryPartner);
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            //add order to given partner's order list
            partnerToOrderMap.putIfAbsent(partnerId, new HashSet<>());
            partnerToOrderMap.get(partnerId).add(orderId);
            //increase order count of partner
            DeliveryPartner deliveryPartner = partnerMap.get(partnerId);
            deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()+1);
            //assign partner to this order
            orderToPartnerMap.put(orderId, partnerId);
        }
    }
    public boolean isOrderExist(String orderId) {
        return orderMap.containsKey(orderId);
    }

    public boolean isPartnerExist(String partnerId) {
        return partnerMap.containsKey(partnerId);
    }

    public Order findOrderById(String orderId){
        // your code here
        if(orderMap.containsKey(orderId)){
            return orderMap.get(orderId);
        }
        return null;
    }
//
    public DeliveryPartner findPartnerById(String partnerId) {
        // your code here
        if (partnerMap.containsKey(partnerId)){
            return partnerMap.get(partnerId);
        }
        return null;
    }

    public Integer findCountOfUnassignedOrders() {
        int totalOrders = orderMap.size();
        int assignedOrders = orderToPartnerMap.size();
        return totalOrders-assignedOrders;
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
        if(partnerToOrderMap.containsKey(partnerId)){
            return partnerToOrderMap.get(partnerId).size();
        }
        else return 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        if(partnerToOrderMap.containsKey(partnerId)){
            List<String> orders = new ArrayList<>(partnerToOrderMap.get(partnerId));
            return orders;
        }
        return new ArrayList<>();
    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
        List<String> allOrders = new ArrayList<>(orderMap.keySet());
        return allOrders;
    }

    public void deletePartner(String partnerId){
        // your code here
        // delete partner by ID
        if(!partnerMap.containsKey(partnerId)){
            throw new RuntimeException("PartnerId not found");
        }

        List<String> orders= new ArrayList<>(partnerToOrderMap.get(partnerId));
        for(String orderId : orders){
            orderToPartnerMap.remove(orderId);
        }

        partnerMap.remove(partnerId);
        partnerToOrderMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        // your code here
        // delete order by ID
        if(!orderMap.containsKey(orderId)){
            throw new RuntimeException("OrderId not found");
        }

        if(orderToPartnerMap.containsKey(orderId)){
            String partnerId = orderToPartnerMap.get(orderId);

            if(partnerToOrderMap.containsKey(partnerId)){
                partnerToOrderMap.get(partnerId).remove(orderId);

                DeliveryPartner partner = partnerMap.get(partnerId);
                partner.setNumberOfOrders(partner.getNumberOfOrders()-1);
            }
            orderToPartnerMap.remove(orderId);
        }
        orderMap.remove(orderId);
    }


    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        if(partnerToOrderMap.containsKey(partnerId)){
            int count=0;
            int time = Integer.parseInt(timeString);
            for(String orderId: partnerToOrderMap.get(partnerId)){
                if(orderMap.get(orderId).getDeliveryTime() > time){
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        if(partnerToOrderMap.containsKey(partnerId)){
            int max=0;
            for(String orderId : partnerToOrderMap.get(partnerId)){
                if(orderMap.get(orderId).getDeliveryTime() > max){
                    max = orderMap.get(orderId).getDeliveryTime();
                }
            }

            int hours = max/60;
            int mins = max - (hours*60);
            String time = hours+ ":" + mins;
            return time;
        }
        return "";
    }
}