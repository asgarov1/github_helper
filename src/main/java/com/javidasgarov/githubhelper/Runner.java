package com.javidasgarov.githubhelper;

import com.javidasgarov.githubhelper.job.GitHubJob;

import java.io.IOException;

public class Runner {
    public static void main(String[] args) throws IOException {
        new GitHubJob();
    }
}
