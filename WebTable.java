package com.mbrdi.tld.AbstractedPageComponent;

import com.mbrdi.tld.Pages.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by kprusty on 10/18/2017.
 */
public class WebTable_new extends Base{

    static By _table_locator ;
    static String _xpath_row_ng_repeat ;

    public WebTable_new(By _table_locator, String _xpath_row_ng_repeat){
        this._table_locator = _table_locator;
        this._xpath_row_ng_repeat = _xpath_row_ng_repeat;
    }

    public static String get_cell_data(int row, int col){
        int td ;
        td = (row - 1)* get_table_column_count() + col ;
        String _reqd_xapth = "("+ get_xpath_expression_from_By(_table_locator) + "//tr[@ng-repeat='"+_xpath_row_ng_repeat+"']/td)["+td+"]" ;
        return driver.findElement(By.xpath(_reqd_xapth)).getText();
    }
    /**
     * @return -- list of column headers of the table
     */
    public static List<String> get_all_table_column_headers(){
        List<String> _col_headers = new ArrayList<>() ;
        for (WebElement we : driver.findElements(By.xpath(get_xpath_expression_from_By(_table_locator)+"//th"))){
            _col_headers.add(we.getText());
        }
        return _col_headers;
    }

    /**
     * @return -- counts the number of columns in the table
     */
    public static int get_table_column_count(){
        return get_all_table_column_headers().size();
    }

    /**
     * @return -- the number of rows present in the table
     */
    public static int get_table_row_count(){
        int _curr_row_ctr ;
        _curr_row_ctr = driver.findElements(By.xpath(get_xpath_expression_from_By(_table_locator)+"//tr")).size();
        return _curr_row_ctr - 1 ; // minus 1 as there would be 1 row corresponding to table header
    }

    /**-
     * @param _col_name
     * @return -- position of column in the table
     */
    public static int get_table_column_position(String _col_name){
        return get_all_table_column_headers().indexOf(_col_name) + 1 ;// adding 1 as List is 0 based index
    }

    public static int get_table_row_num_based_on_search_keyword(String col_name, String _comp_name){
        return get_all_values_by_column_name(col_name).indexOf(_comp_name) + 2;    //adding 2 as List is 0 based and the 1st row is header
    }

    /**
     *  the tr changes in all table implementations when getting all the values from a column.
     *  This tr-repeat pattern can be used to make a generic API for this
     */
    public static List<String> get_all_values_by_column_name(String _col_name){
        // extract position of the column i.e. ComponentName = 1
        List<String> _val_col_wise = new ArrayList<>();
        int _col_position = get_table_column_position(_col_name) ;
        int _num_of_cols = get_table_column_count();
        String _xpath_builder;

        for (int i=1; i < get_table_row_count(); i++){
            _xpath_builder = "("+ get_xpath_expression_from_By(_table_locator) + "//tr[@ng-repeat='"+_xpath_row_ng_repeat+"']/td)["+_col_position+ "]" ;
            _val_col_wise.add(
                    driver.findElement(By.xpath(_xpath_builder)).getText()
            );
            _col_position += _num_of_cols;
        }
        return _val_col_wise;
    }

    /**
     * @param _col_name
     * @param _cell_val
     * @return - xpath for the cell
     */
    public static String generate_xpath_expression_dynamically(String _col_name, String _cell_val){
        int _col_position = get_table_column_position(_col_name) ;
        int _num_of_cols = get_table_column_count();
        String _xpath_builder;

        for (int i=1; i <= get_table_row_count(); i++){
            _xpath_builder = "("+ get_xpath_expression_from_By(_table_locator) + "//tr[@ng-repeat='"+_xpath_row_ng_repeat+"']/td)["+_col_position+"]" ;
            if (_cell_val.equals(driver.findElement(By.xpath(_xpath_builder)).getText()))
                return _xpath_builder;
            _col_position += _num_of_cols;
        }
        return null;
    }

    public static String get_xpath_expression_from_By(By xpath){
        String[] _xpath_split = xpath.toString().split(":");
        return _xpath_split[1].trim();
    }

    public static void click_select_cell_in_table(String _col_name, String _cell_val) {
        String _xpath = generate_xpath_expression_dynamically(_col_name, _cell_val);
        driver.findElement(By.xpath(_xpath)).click();
    }

    // xpath that we get is like: --> //div[@class='openOrderListTable']/table//tr[@ng-repeat='order in getAllOrder']/td)[18]

    /**
     * @param _primary_col_header
     * @param _primary_col_val
     * @param _query_col
     * @return - value from a column mapped to a different column's value
     * limitation - if _primary_col_header is ahead of the query col, this method wil not be applicable. As of now this situation has not come
     */
    public static String get_mapped_value(String _primary_col_header, String _primary_col_val, String _query_col){
        int _td_offset = Math.abs(get_table_column_position(_primary_col_header) - get_table_column_position(_query_col));
        String _xpath_of_primary_cell_val = generate_xpath_expression_dynamically(_primary_col_header, _primary_col_val);
        String[] a = _xpath_of_primary_cell_val.split(Pattern.quote("["));
        String[] b = a[a.length -1].split(Pattern.quote("]"));
        int td_of_primary_col_val = Integer.parseInt(b[0]);
        int td_of_reqd = td_of_primary_col_val + _td_offset;
        String _reqd_xapth = "("+ get_xpath_expression_from_By(_table_locator) + "//tr[@ng-repeat='"+_xpath_row_ng_repeat+"']/td)["+td_of_reqd+"]" ;
        return driver.findElement(By.xpath(_reqd_xapth)).getText();
    }
}
