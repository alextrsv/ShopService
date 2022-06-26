package shop.entities.enums;

public enum ItemType {
    CATEGORY("CATEGORY"), OFFER("OFFER");

    private String name;

    ItemType() {}

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
