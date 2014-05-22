package com.tsif.publiccalendarviewer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.yaml.snakeyaml.Yaml;

public class CommunitiesParser {

	ArrayList<CommunityEntry> _entries;
	
	class Maintainer {
	
		public String name;
		public String website;
		public String email;
		
		public Maintainer(String name, String website, String email) {
			this.name    = name;
			this.website = website;
			this.email   = email;
			
		}
	};
	
    class CommunityEntry {
		 
    	public String name;
    	public String period;
    	public String happensAt;
    	public String description;
    	public String website;
    	public String twitter;
    	public String facebook;
    	
    	public List<Maintainer> maintainers;

    	public CommunityEntry() {
    		maintainers = new ArrayList<CommunitiesParser.Maintainer>();
    	}
	};
	
	@SuppressWarnings("unchecked")
	public CommunitiesParser(String input) throws JSONException {
		 
		_entries                             = new ArrayList<CommunitiesParser.CommunityEntry>();
		
		Yaml                      yaml       = new Yaml();
		List<Map<String, Object>> data       = (List<Map<String, Object>>) yaml.load(input);
		Class<?>                  entryclass = CommunityEntry.class;
				
		for(Object o : data) {
			
			Map<String, Object> m = (Map<String, Object>)o;
			CommunityEntry      c = new CommunityEntry();
			
			for (Map.Entry<String, Object> entry : m.entrySet()) {
				try {
					Field field = entryclass.getField(entry.getKey());
					Object value = entry.getValue();
					if(value instanceof String) { 
						field.set(c, value);
					} else if(value instanceof ArrayList<?>) {
						for(Object ob : (ArrayList<?>)value) {
							if(ob instanceof LinkedHashMap<?, ?>) {
								HashMap<?, ?> hm = (HashMap<?, ?>)ob;
								c.maintainers.add(new Maintainer((String)hm.get("name"), (String)hm.get("website"), (String)hm.get("email")));
							}
						}
					}
				} catch (NoSuchFieldException e) {
			    } catch (Exception e) {}
			}
			_entries.add(c);
		}
	}
	
	public ArrayList<CommunityEntry> getEntries() {
        return _entries;
	}
}
