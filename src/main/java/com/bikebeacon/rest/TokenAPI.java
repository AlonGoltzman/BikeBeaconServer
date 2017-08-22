package com.bikebeacon.rest;

import com.bikebeacon.cch.CCHDelegate;
import com.bikebeacon.cch.CentralControlHub;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static com.bikebeacon.utils.PrintUtil.error;

@WebServlet("/token_api")
public class TokenAPI extends CCHDelegate {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder tokenBuilder = new StringBuilder();
        try (InputStream in = request.getInputStream()) {
            int aRead;
            byte[] buffer = new byte[1024];

            while ((aRead = in.read(buffer)) != -1)
                tokenBuilder.append(new String(buffer, 0, aRead));
        }

        if (tokenBuilder.toString().isEmpty()) {
            response.sendError(400);
            error("TokenAPI->doPost", "Invalid token");
        }

        CCH.setToken(request.getRemoteAddr(), tokenBuilder.toString());
    }
}
