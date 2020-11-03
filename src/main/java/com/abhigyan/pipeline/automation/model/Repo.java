package com.abhigyan.pipeline.automation.model;

import java.util.List;

import lombok.Data;

@Data
public class Repo {
	private List<String> allBranches;
	private List<String> allBranchesSHA;
}
