package com.javidasgarov.githubhelper;

import lombok.Data;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.List;

@Data
public class RepositoryDescription {
    private final String name;
    private final GHRepository repository;
    private final List<GHPullRequest> pullRequests;
}
