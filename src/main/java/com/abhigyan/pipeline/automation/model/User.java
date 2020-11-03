package com.abhigyan.pipeline.automation.model;

import java.util.List;

import lombok.Data;

@Data
public class User {
	private int userId;
	private String loginName;
	private String repoURL;
	private List<String> repoList;
}