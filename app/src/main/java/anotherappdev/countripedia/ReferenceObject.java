package anotherappdev.countripedia;

/**
 * Created by Aditya on 03-06-2018.
 */

//Structure of data in the CardView

public class ReferenceObject {
    private String referenceName;
    private String referenceLink;

    ReferenceObject(String name, String link) {
        referenceName = name;
        referenceLink = link;
    }

    public String getmText1() {
        return referenceName;
    }

    public void setmText1(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getmText2() {
        return referenceLink;
    }

    public void setmText2(String referenceLink) {
        this.referenceLink = referenceLink;
    }
}