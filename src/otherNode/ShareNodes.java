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
     *
     * @param maybeNew
     */
    public void maybeAddInterestedSon(InfoNodo maybeNew) {
        ArrayList<InfoNodo> copyInterestedSons = new ArrayList<>(interestedSons);
        boolean alreadyInterested = copyInterestedSons.stream().anyMatch(oneSon -> (oneSon.ip.equals(maybeNew.ip) && maybeNew.portNet == oneSon.portNet));
        if (!alreadyInterested) System.out.println("Adiciona filho interessado");
        if (!alreadyInterested) interestedSons.add(maybeNew);
    }
}
