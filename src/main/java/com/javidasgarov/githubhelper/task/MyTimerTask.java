package com.javidasgarov.githubhelper.task;

import com.javidasgarov.githubhelper.RepositoryDescription;
import com.javidasgarov.githubhelper.gui.GuiService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@RequiredArgsConstructor
public class MyTimerTask extends TimerTask {

    private final GHMyself myself;
    private final Set<Long> allPullRequestIds = new HashSet<>();
    private final GuiService gui = new GuiService();
    boolean shouldShowNotification;

    @SneakyThrows
    @Override
    public void run() {
        shouldShowNotification = !allPullRequestIds.isEmpty();

        List<RepositoryDescription> repos = myself.getAllRepositories()
                .values()
                .stream()
                .map(repository -> {
                    try {
                        return getPRs(repository);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        gui.setMenu(myself.getLogin(), repos);
    }

    private RepositoryDescription getPRs(GHRepository repository) throws IOException {
        List<GHPullRequest> pullRequests = repository.queryPullRequests()
                .list()
                .toList();

        Set<Long> newPullRequestIds = getNewPullRequestIds(pullRequests);
        allPullRequestIds.addAll(newPullRequestIds);

        Set<GHPullRequest> newPRs = pullRequests.stream()
                .filter(pr -> newPullRequestIds.contains(pr.getId()))
                .collect(Collectors.toSet());

        if (shouldShowNotification) {
            newPRs.forEach(gui::showNotification);
        }

        return new RepositoryDescription(
                repository.getFullName(),
                repository,
                pullRequests
        );
    }

    private Set<Long> getNewPullRequestIds(List<GHPullRequest> pullRequests) {
        return pullRequests.stream()
                .map(GHPullRequest::getId)
                .filter(not(allPullRequestIds::contains))
                .collect(Collectors.toSet());
    }
}
