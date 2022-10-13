package io.project.SpringTelegramGHActivityBot.data;

public class RequestRepository {

    private Long id;
    private String name;
    private String full_name;
    private String url;
    private String html_url;
    private String description;

    private RequestRepository(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.full_name = builder.full_name;
        this.url = builder.url;
        this.html_url = builder.html_url;
        this.description = builder.description;
    }

    public static class Builder {

        private Long id;
        private String name;
        private String full_name;
        private String url;
        private String html_url;
        private String description;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setFullName(String FullName) {
            this.full_name = FullName;
            return this;
        }

        public Builder setURL(String URL) {
            this.url = URL;
            return this;
        }

        public Builder setHTML_URL(String HTML_URL) {
            this.html_url = HTML_URL;
            return this;
        }

        public Builder setDescription(String Description) {
            this.description = Description;
            return this;
        }

        public RequestRepository build() {
            return new RequestRepository(this);
        }

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return full_name;
    }

    public String getDescription() {
        return description;
    }

    public String getURL() {
        return url;
    }

    public String getHTML_URL() {
        return html_url;
    }

    public String toString() {
        return "Repository[%s, %s, %s, %s]"
                .formatted(getId(), getFullName(), getDescription(), getHTML_URL());
    }

    public String getOwnerName() {
//        return getFullName().substring(0, getFullName().lastIndexOf("/"));
        return getFullName().substring(0, getFullName().indexOf("/"));
    }

    public void setName(String name) {
        this.name = name;
    }
}