package io.project.SpringTelegramGHActivityBot.data;

public class PullRequest {

    public Long id;
    public String title;
    public String user_login;
    public String html_url;
    public String body;
    public String created_at;
    public String updated_at;

    public User user;

    public static class User {
        public String login;

        public User(String login) {
            this.login = login;
        }

        public String getUserLogin() {
            return this.login;
        }
    }

    public static class Builder {
        private Long id;
        private String title;
        private String user_login;
        private String html_url;
        private String body;
        private String created_at;
        private String updated_at;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setUSER_LOGIN(String login) {
            this.user_login = login;
            return this;
        }

        public Builder setHTML_URL(String url) {
            this.html_url = url;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setCREATED_AT(String created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder setUPDATED_AT(String updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public PullRequest build() {
            return new PullRequest(this);
        }
    }

    private PullRequest(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.user_login = builder.user_login;
        this.html_url = builder.html_url;
        this.body = builder.body;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUSER_LOGIN() {
        return this.user_login;
    }

    public String getHTML_URL() {
        return this.html_url;
    }

    public String getBody() {
        return this.body;
    }

    public String getCREATED_AT() {
        return this.created_at;
    }

    public String getUPDATED_AT() {
        return this.updated_at;
    }
}