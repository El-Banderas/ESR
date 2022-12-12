package otherNode;

import Common.InfoNodo;

import java.util.ArrayList;

public class ShareNodes {
    public ArrayList<InfoNodo> interestedSons;

    public ShareNodes() {
        this.interestedSons = new ArrayList<>();
    }

    /**
     * If the node isn't registed in the interested sons, we store it.
     * Otherwise, we ignore it.
     * @param maybeNew
     */
    public void maybeAddInterestedSon(InfoNodo maybeNew) {
        boolean alreadyInterested = interestedSons.stream().anyMatch(oneSon -> InfoNodo.compareInfoNodes(oneSon, maybeNew));
        if (!alreadyInterested) interestedSons.add(maybeNew);

    }
}
