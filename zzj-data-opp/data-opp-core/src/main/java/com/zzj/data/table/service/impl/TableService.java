package com.zzj.data.table.service.impl;

import com.zzj.data.core.DbBuilder;
import com.zzj.data.core.DbDatasource;
import com.zzj.data.error.DataException;
import com.zzj.data.params.DbParams;
import com.zzj.data.params.FieldObj;
import com.zzj.data.params.TableObj;
import com.zzj.data.params.TableParams;
import com.zzj.data.registion.entity.SysCommDb;
import com.zzj.data.registion.service.IRegistionService;
import com.zzj.data.table.service.ITableService;
import com.zzj.data.table.vo.FiledColumnVO;
import com.zzj.data.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.*;

@Service
public class TableService implements ITableService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${template.export.column}")
    private String excelPath;

    @Autowired
    private IRegistionService registionService;

    @Override
    public List<FieldObj> getTableColumn(String dataSourceId, String tableName) {
        SysCommDb db = registionService.getDetailById(dataSourceId);
        List<FieldObj> list = new ArrayList<>();
        try {
            DbDatasource dbDatasource = DbBuilder.build(getParams(db));
            TableObj tableObj = new TableObj();
            tableObj.setUserDbName(getOwner(db));
            tableObj.setTableName(tableName);
            list = dbDatasource.queryFieldList(tableObj);
        } catch (Exception e) {
            logger.error("数据源异常：", e);
            throw new DataException("执行数据操作时出现异常：", e);
        }
        return list;
    }

    @Override
    public List<FiledColumnVO> getColumnData(String dataSourceId, String tableName, int size) {
        SysCommDb db = registionService.getDetailById(dataSourceId);
        List<FieldObj> list = new ArrayList<>();
        List<FiledColumnVO> filedColumnVOList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        try {
            DbDatasource dbDatasource = DbBuilder.build(getParams(db));
            TableObj tableObj = new TableObj();
            tableObj.setUserDbName(getOwner(db));
            tableObj.setTableName(tableName);
            list = dbDatasource.queryFieldList(tableObj);
            if (db.getType().equals("102") && tableObj.getUserDbName().indexOf("-") != -1) {
                tableObj.setUserDbName("");
            } else {
                tableObj.setUserDbName(tableObj.getUserDbName() + ".");
            }
            if (CollectionUtils.isNotEmpty(list)) {
                FiledColumnVO filedColumnVO = null;
                for (FieldObj fieldObj : list) {
                    filedColumnVO = new FiledColumnVO();
                    filedColumnVO.setField(fieldObj);
                    filedColumnVO.setColumnData(dbDatasource.getColumnData(tableObj.getUserDbName(), tableObj.getTableName(), fieldObj.getColumnName(), size));
                    filedColumnVOList.add(filedColumnVO);
                }
            }
        } catch (Exception e) {
            logger.error("数据源异常：", e);
            throw new DataException("执行数据操作时出现异常：", e);
        }

        return filedColumnVOList;
    }

    @Override
    public List<LinkedHashMap<String, Object>> preData(String dataSourceId, String tableName, int size) {
        SysCommDb db = registionService.getDetailById(dataSourceId);
        try {
            DbDatasource dbDatasource = DbBuilder.build(getParams(db));
            TableParams tableObj = new TableParams();
            tableObj.setSchemaname(getOwner(db));
            tableObj.setTableName(tableName);
            return dbDatasource.preQueryData(tableObj, size);
        } catch (Exception e) {
            logger.error("数据源异常：", e);
            throw new DataException("执行数据操作时出现异常：", e);
        }
    }


    private String getOwner(SysCommDb db) {
        String owner = null;
        switch (Integer.parseInt(db.getType())) {
            case Constants.DB_SOURCE.MYSQL_TYPE:
                owner = db.getDbName();
                break;
            case Constants.DB_SOURCE.ORACLE_TYPE:
                owner = db.getAccount();
                break;
            default:
                break;

        }
        return owner;
    }

    private DbParams getParams(SysCommDb db) {
        String url = null;
        DbParams params = new DbParams();
        switch (Integer.parseInt(db.getType())) {
            case Constants.DB_SOURCE.MYSQL_TYPE:
                url = "jdbc:mysql://" + db.getHost() + ":" + db.getPort() + "/" + db.getDbName() + "?characterEncoding=UTF-8";
                break;
            case Constants.DB_SOURCE.ORACLE_TYPE:
                url = "jdbc:oracle:thin:@" + db.getHost() + ":" + db.getPort() + ":" + db.getDbName() + "";
                break;
            default:
                break;

        }
        params.setUser(db.getAccount());
        params.setPassword(db.getPassword());
        params.setUrl(url);
        params.setMaxActive(1);
        params.setType(db.getType());
        return params;
    }

    @Override
    public void exportColumnData(String dataSourceId, String tableName, int size) {
        List<FiledColumnVO> dataList = new ArrayList<>();
        SysCommDb db = registionService.getDetailById(dataSourceId);
        List<FieldObj> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        try {
            DbDatasource dbDatasource = DbBuilder.build(getParams(db));
            TableObj tableObj = new TableObj();
            tableObj.setUserDbName(getOwner(db));
            tableObj.setTableName(tableName);
            list = dbDatasource.queryFieldList(tableObj);
            if (db.getType().equals("102") && tableObj.getUserDbName().indexOf("-") != -1) {
                tableObj.setUserDbName("");
            } else {
                tableObj.setUserDbName(tableObj.getUserDbName() + ".");
            }
            if (CollectionUtils.isNotEmpty(list)) {
                FiledColumnVO filedColumnVO = null;
                for (FieldObj fieldObj : list) {
                    filedColumnVO = new FiledColumnVO();
                    filedColumnVO.setField(fieldObj);
                    filedColumnVO.setColumnData(dbDatasource.getColumnData(tableObj.getUserDbName(), tableObj.getTableName(), fieldObj.getColumnName(), size));
                    dataList.add(filedColumnVO);
                }
            }
        } catch (Exception e) {
            logger.error("数据源异常：", e);
            throw new DataException("执行数据操作时出现异常：", e);
        }


        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        String path = this.getClass().getClassLoader().getResource(excelPath).getPath();

        FileInputStream fs = null;
        OutputStream out = null;
        Row row;
        Sheet sheet;
        try {
            fs = new FileInputStream(new File(URLDecoder.decode(path, "UTF-8")));
            XSSFWorkbook book = new XSSFWorkbook(fs);
            CellStyle cellStyle = book.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.MEDIUM);
            cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderLeft(BorderStyle.MEDIUM);
            cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderTop(BorderStyle.MEDIUM);
            cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setBorderRight(BorderStyle.MEDIUM);
            cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
            out = response.getOutputStream();
            response.setHeader("content-disposition", "attachment;filename=" + new String("数据稽查".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            response.setContentType("application/msexcel");

            if (CollectionUtils.isNotEmpty(dataList)) {
                sheet = book.getSheetAt(0);
                int count = 1;
                for (FiledColumnVO filedColumnVO : dataList) {
                    row = sheet.createRow(count);
                    setCellVal(row, 0, db.getDbName(), cellStyle);
                    setCellVal(row, 1, db.getHost(), cellStyle);
                    setCellVal(row, 2, db.getPort(), cellStyle);
                    setCellVal(row, 3, db.getAccount(), cellStyle);
                    setCellVal(row, 4, db.getPassword(), cellStyle);
                    setCellVal(row, 5, tableName, cellStyle);
                    setCellVal(row, 6, filedColumnVO.getField().getColumnName(), cellStyle);
                    setCellVal(row, 7, filedColumnVO.getField().getType(), cellStyle);
                    setCellVal(row, 8, filedColumnVO.getField().getLength(), cellStyle);
                    if ("1".equals(filedColumnVO.getField().getDbKeyFlg())) {
                        setCellVal(row, 9, "否", cellStyle);
                    } else {
                        setCellVal(row, 9, "是", cellStyle);
                    }

                    setCellVal(row, 10, filedColumnVO.getField().getColIndex(), cellStyle);
                    setCellVal(row, 11, filedColumnVO.getField().getPartitions(), cellStyle);
                    setCellVal(row, 12, filedColumnVO.getField().getComment(), cellStyle);
                    if (CollectionUtils.isNotEmpty(filedColumnVO.getColumnData())) {
                        for (int i = 0; i < size; i++) {
                            if (filedColumnVO.getColumnData().size() < size) {
                                if (i > filedColumnVO.getColumnData().size() - 1) {
                                    setCellVal(row, 13 + i, "", cellStyle);
                                } else {
                                    setCellVal(row, 13 + i, filedColumnVO.getColumnData().get(i), cellStyle);
                                }
                            } else {
                                setCellVal(row, 13 + i, filedColumnVO.getColumnData().get(i), cellStyle);
                            }
                        }
                    } else {
                        for (int i = 0; i < size; i++) {
                            setCellVal(row, 13 + i, "", cellStyle);
                        }
                    }
                    count++;
                }


            }
            book.write(out);
            out.flush();
        } catch (Exception e) {
            logger.error("导出异常", e);
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.error("关闭IO异常", e);
            }
        }
    }

    /**
     * 给单元格设值
     *
     * @param row
     * @param cellIndex
     * @param val
     * @param style
     */
    private void setCellVal(Row row, int cellIndex, Object val, CellStyle style) {
        Cell cell;
        if (row.getCell(cellIndex) == null) {
            cell = row.createCell(cellIndex);
        } else {
            cell = row.getCell(cellIndex);
        }
        cell.setCellStyle(style);
        if (val != null) {
            cell.setCellValue(String.valueOf(val));
        } else {
            cell.setCellValue("");
        }
    }
}
