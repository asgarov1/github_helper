package com.javidasgarov.githubhelper.job;

import com.javidasgarov.githubhelper.task.MyTimerTask;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.Timer;

public class GitHubJob {
    private final GitHub gitHub;

    public GitHubJob() throws IOException {
        gitHub = new GitHubBuilder()
                .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                .build();
        init();
    }

    private void init() throws IOException {
        GHMyself myself = gitHub.getMyself();

        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(myself), 1000, 1000);
    }
}
