package com.sparkproject.OfferJet.customprocessor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
	
	// Offer 2: Free Shipping for amount > 100
	public class Offer2Processor implements ItemProcessor<Map<String, Object>, Map<String, Object>> {
		
		private static final Logger logger = LoggerFactory.getLogger(Offer2Processor.class);
		
	    @Override
	    public Map<String, Object> process(Map<String, Object> item) {
	        Integer amount = (Integer) item.get("amount");
	        logger.info("=================== Processing item with amount: {} ===================", amount);
	        
	        if (amount != null && amount > 100) {
	            item.put("offer", "Free Shipping");
	        } else {
	            item.put("offer", "Standard Shipping");
	        }
	        return item;
	    }
}
