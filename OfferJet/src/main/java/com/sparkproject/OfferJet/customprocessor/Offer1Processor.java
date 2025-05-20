package com.sparkproject.OfferJet.customprocessor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

//Offer 1: 10% Discount for amount <= 100
public class Offer1Processor implements ItemProcessor<Map<String, Object>, Map<String, Object>>  {

	private static final Logger logger = LoggerFactory.getLogger(Offer1Processor.class);

	@Override
	public Map<String, Object> process(Map<String, Object> item) throws Exception {
		Integer amount = (Integer) item.get("amount");
		logger.info("=================== Processing item with amount: {} ===================", amount);

		if(amount != null && amount <= 100) {
			item.put("offer", "10% Discount");
			logger.info("=================== Applied offer: 10% Discount ===================");
		} else {
			item.put("offer", "No Offer");
		}
		
		return item;
	}
}

