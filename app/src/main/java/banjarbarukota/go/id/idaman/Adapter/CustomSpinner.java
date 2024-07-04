package banjarbarukota.go.id.idaman.Adapter;


public class CustomSpinner {
    private String itemId, name, imageIcon;

    public CustomSpinner(String itemId, String name, String imageIcon){
        this.itemId = itemId;
        this.name = name;
        this.imageIcon = imageIcon;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }


    public String getImageIcon() {
        return imageIcon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }
}
