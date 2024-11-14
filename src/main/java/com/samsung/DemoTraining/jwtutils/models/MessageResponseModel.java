package com.samsung.DemoTraining.jwtutils.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseModel {
	private Boolean status;
	private String message;
	
}
