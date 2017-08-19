package com.bikebeacon.cch;

import com.bikebeacon.utils.AssetsUtil;
import com.bikebeacon.utils.ConversationUtil;

import java.util.ArrayList;

import static com.bikebeacon.utils.PrintUtil.error;
import static com.bikebeacon.utils.PrintUtil.log;

public final class CentralControlHub implements CaseHandler {

    private ArrayList<Case> allActiveCases;
    private ArrayList<CCHDelegate> delegates;

    private static CentralControlHub instance;

    public static CentralControlHub getCCH() {
        return instance == null ? new CentralControlHub() : instance;
    }

    private CentralControlHub() {
        instance = this;
        allActiveCases = new ArrayList<>();
        delegates = new ArrayList<>();
    }

    public void entrustCase(CCHDelegate creator, Case newCase) {
        if (!delegates.contains(creator)) {
            error("CCH->entrustCase", "Unknown CCHDelegate.");
            return;
        }
        if (newCase.isActive()) {
            newCase.setCaseHandler(this);
            allActiveCases.add(newCase);
            beginSequence(newCase);
        } else
            log("###   CCH   ###", "Tried entrusting inactive case, use archiveCase instead.");
    }

    public void archiveCase(Case doneCase) {
        if (allActiveCases.contains(doneCase))
            allActiveCases.remove(doneCase);
        else
            log("###   CCH   ###", "Tried archiving a case that doesn't exist in the active cases, will archive anyway, but take note.");
        AssetsUtil.save(doneCase.fileify());
    }

    public void notifyDelegateCreation(CCHDelegate delegate) {
        delegates.add(delegate);
    }

    public void notifyDelegateEliminated(CCHDelegate delegate) {
        delegates.remove(delegate);
    }

    private void beginSequence(Case caseToInitiate) {
        caseToInitiate.setJerry(new ConversationUtil());
    }


}
