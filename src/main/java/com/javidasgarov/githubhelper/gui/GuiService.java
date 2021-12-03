package com.javidasgarov.githubhelper.gui;

import com.javidasgarov.githubhelper.RepositoryDescription;
import lombok.SneakyThrows;
import org.kohsuke.github.GHPullRequest;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static java.awt.TrayIcon.MessageType.INFO;

public class GuiService {

    public static final String HELPER = "GitHub helper";
    private TrayIcon trayIcon;

    @SneakyThrows
    public GuiService() {
        SystemTray tray = SystemTray.getSystemTray();
        tray.add(createTrayIcon());
    }

    private TrayIcon createTrayIcon() {
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/hammer.png"));
        trayIcon = new TrayIcon(image, HELPER);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(HELPER);
        return trayIcon;
    }

    public void showNotification(String title, String text) {
        trayIcon.displayMessage(title, text, INFO);
    }

    public void showNotification(GHPullRequest pr) {
        showNotification("New PR in " + pr.getRepository().getFullName(), pr.getTitle());
    }

    public void setMenu(String login, List<RepositoryDescription> repos) {
        PopupMenu popup = new PopupMenu();

        popup.add(createAccountMenuItem(login));
        popup.addSeparator();
        popup.add(createNotificationMenuItem());
        popup.add(createReposMenuItem(repos));

        trayIcon.setPopupMenu(popup);
    }

    private MenuItem createReposMenuItem(List<RepositoryDescription> repos) {
        Menu repositoriesMenuItem = new Menu("repositories");
        repos.stream()
                .map(this::createRepoMenuItem)
                .forEach(repositoriesMenuItem::add);
        return repositoriesMenuItem;
    }

    private MenuItem createRepoMenuItem(RepositoryDescription repo) {
        Menu repoSubMenu = new Menu(getName(repo));

        repoSubMenu.add(createOpenInBrowserMenuItem(repo));
        if (!repo.getPullRequests().isEmpty()) {
            repoSubMenu.addSeparator();
        }

        repo.getPullRequests()
                .forEach(pr -> {
                    MenuItem prMI = new MenuItem(pr.getTitle());
                    prMI.addActionListener(e ->
                            openInBrowser(pr.getHtmlUrl().toString())
                    );
                    repoSubMenu.add(prMI);
                });

        return repoSubMenu;
    }

    private MenuItem createOpenInBrowserMenuItem(RepositoryDescription repo) {
        MenuItem openInBrowserMenuItem = new MenuItem("Open in browser");
        openInBrowserMenuItem.addActionListener(e
                -> openInBrowser(repo.getRepository().getHtmlUrl().toString()));
        return openInBrowserMenuItem;
    }

    private String getName(RepositoryDescription repo) {
        return repo.getPullRequests().size() > 0
                ? String.format("(%d) %s", repo.getPullRequests().size(), repo.getName())
                : repo.getName();
    }

    private MenuItem createAccountMenuItem(String login) {
        MenuItem accountMenuItem = new MenuItem(login);
        accountMenuItem.addActionListener(e -> openInBrowser("https://github.com/" + login));
        return accountMenuItem;
    }

    private MenuItem createNotificationMenuItem() {
        MenuItem accountMenuItem = new MenuItem("notifications");
        accountMenuItem.addActionListener(e -> openInBrowser("https://github.com/notifications"));
        return accountMenuItem;
    }

    @SneakyThrows
    public void openInBrowser(String url) {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URL(url).toURI());
    }
}
