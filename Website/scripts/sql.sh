cd "$MANASCAPE_HOME/Website/sql/1.0"

mysql -uroot -pmysql < 1.0-DDL.sql
mysql -uroot -pmysql < 1.0-DML.sql