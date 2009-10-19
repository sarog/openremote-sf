package org.openremote.beehive.db;

import java.sql.Types;

import org.hibernate.dialect.MySQL5Dialect;
/**
 * A customized SQL dialect for MySQL 5.x specific features.
 * 
 * @author Dan 2009-2-6
 *
 */
public class CustomMySQL5Dialect extends MySQL5Dialect {
	
	public CustomMySQL5Dialect() {
		super();
		//maps the boolean type to tinyint(1)
		registerColumnType(Types.BIT, "tinyint(1)");
	}

	/**
	 * Sets the MySQL default charset to UTF-8
	 */
	@Override
	public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
	
	
}
