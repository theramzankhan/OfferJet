package com.sparkproject.OfferJet.customprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class Offer1ProcessorTest {
private final Offer1Processor processor = new Offer1Processor();
	
	@Test
	void shouldApplyOfferForLowValueCustomer() throws Exception {
		Map<String, Object> input = new HashMap<>();
		input.put("amount", 90);
		
		Map<String, Object> result = processor.process(input);
		
		assertEquals("10% Discount", result.get("offer"));
	}
	
	@Test
	void shouldNotApplyOfferForLowValueCustomer() throws Exception {
		Map<String, Object> input = new HashMap<>();
		input.put("amount", 200);
		
		Map<String, Object> result = processor.process(input);
		
		assertEquals("No Offer", result.get("offer"));
	}
}
