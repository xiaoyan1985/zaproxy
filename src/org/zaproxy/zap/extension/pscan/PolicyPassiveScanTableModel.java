/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2011 The Zed Attack Proxy team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.zaproxy.zap.extension.pscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.scanner.Plugin.AlertThreshold;
import org.parosproxy.paros.core.scanner.Plugin.AttackStrength;


public class PolicyPassiveScanTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	private Map<String, String> i18nToStr = null;
    private static final String[] columnNames = {
									Constant.messages.getString("ascan.policy.table.testname"), 
									Constant.messages.getString("ascan.policy.table.threshold")};

    private List<PluginPassiveScanner> listScanners = new ArrayList<>();
    
    /**
     * 
     */
    public PolicyPassiveScanTableModel() {
    }
    
    public void addScanner (PluginPassiveScanner scanner) {
        listScanners.add(scanner);
        fireTableDataChanged();
    }
    
    public void removeScanner (PluginPassiveScanner scanner) {
        listScanners.remove(scanner);
        fireTableDataChanged();
    }
    
    @Override
	public Class<?> getColumnClass(int c) {
        return String.class;
        
    }
    
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            return true;
        }
        return false;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        
    	PluginPassiveScanner test = listScanners.get(row);
        switch (col) {
        	case 0:	break;
        	case 1: AlertThreshold af = AlertThreshold.valueOf(i18nToStr((String)value));
					test.setLevel(af);
					test.setEnabled(!AlertThreshold.OFF.equals(af));
        			test.save();
		            fireTableCellUpdated(row, col);
					break;
        }
    }

    private String strToI18n (String str) {
    	// I18n's threshold and strength enums
    	return Constant.messages.getString("ascan.policy.level." + str.toLowerCase());
    }

    private String i18nToStr (String str) {
    	// Converts to i18n'ed names back to the enum names
    	if (i18nToStr == null) {
    		i18nToStr = new HashMap<String, String>();
    		for (AlertThreshold at : AlertThreshold.values()) {
    			i18nToStr.put(this.strToI18n(at.name()), at.name());
    		}
    		for (AttackStrength as : AttackStrength.values()) {
    			i18nToStr.put(this.strToI18n(as.name()), as.name());
    		}
    	}
    	return i18nToStr.get(str);
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
    	if (listScanners == null) {
    		return 0;
    	}
        return listScanners.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
    	PassiveScanner test = listScanners.get(row);
        Object result = null;
        switch (col) {
        	case 0:	result = test.getName();
        			break;
        	case 1: result = strToI18n(test.getLevel().name());
        			break;
        	default: result = "";
        }
        return result;
    }
}
