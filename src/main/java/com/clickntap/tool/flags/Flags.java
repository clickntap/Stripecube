package com.clickntap.tool.flags;

import com.clickntap.utils.AsciiUtils;
import com.clickntap.utils.ConstUtils;
import com.clickntap.utils.IOUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Flags {

    private JSONObject countries;
    private JSONObject languages;

    public Flags() throws Exception {
        {
            InputStream in = new URL("http://clickntap.s3.amazonaws.com/ui/flags/iso-3166-1.json").openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            out.close();
            in.close();
            countries = new JSONObject(out.toString(ConstUtils.UTF_8)).getJSONObject("Results");
        }
        {
            InputStream in = new URL("http://clickntap.s3.amazonaws.com/ui/flags/iso-639-1.json").openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            out.close();
            in.close();
            languages = new JSONObject(out.toString(ConstUtils.UTF_8));
        }
    }

    public static void main(String args[]) throws Exception {
        int n = 1;
        for (JSONObject item : new Flags().getItems()) {
            System.out.println(n + ") " + item.getJSONObject("Names").get("it") + " (+" + item.get("TelPref") + ") " + item.getJSONObject("CountryCodes").get("iso2"));
            n++;
        }
    }

    public JSONObject getCountries() {
        return countries;
    }

    public void setCountries(JSONObject data) {
        this.countries = data;
    }

    public JSONObject getLanguages() {
        return languages;
    }

    public List<String> getCodes() {
        List<String> codes = new ArrayList<String>();
        String[] names = JSONObject.getNames(countries);
        for (int i = 0; i < names.length; i++) {
            codes.add(names[i]);
        }
        Collections.sort(codes);
        return codes;
    }

    public List<JSONObject> getItems() {
        List<JSONObject> items = new ArrayList<JSONObject>();
        for (String code : getCodes()) {
            JSONObject item = countries.getJSONObject(code);
            try {
                Integer.parseInt(AsciiUtils.phonize(item.getString("TelPref")));
                items.add(item);
            } catch (Exception e) {
            }
        }
        return items;
    }

}
