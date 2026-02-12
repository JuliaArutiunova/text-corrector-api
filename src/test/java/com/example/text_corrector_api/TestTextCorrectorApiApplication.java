package com.example.text_corrector_api;

import org.springframework.boot.SpringApplication;

public class TestTextCorrectorApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TextCorrectorApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
