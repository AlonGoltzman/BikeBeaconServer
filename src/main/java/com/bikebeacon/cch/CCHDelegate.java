package com.bikebeacon.cch;

import com.bikebeacon.pojo.Constants;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;

public abstract class CCHDelegate extends HttpServlet {

    protected CentralControlHub CCH;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        setValues();
        informCCH();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        setValues();
        informCCH();
    }

    private void setValues() {
        Constants.LOG_PATH = new File(getServletContext().getRealPath(""), "/assets/log.bblog");
        Constants.ASSETS_FOLDER = new File(getServletContext().getRealPath(""), "/assets");
    }
    @Override
    public void destroy() {
        super.destroy();
        CentralControlHub.getCCH().notifyDelegateEliminated(this);
    }

    private void informCCH() {
        (CCH = CentralControlHub.getCCH(getServletContext().getRealPath(""))).notifyDelegateCreation(this);
    }
}
