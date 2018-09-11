package com.funny.admin.agent.controller;

import com.alibaba.druid.util.StringUtils;
import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.PageUtils;
import com.funny.utils.Query;
import com.funny.utils.R;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * 卡密信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-07 13:33:51
 */
@RestController
@RequestMapping("cardinfo")
public class CardInfoController {
    @Autowired
    private CardInfoService cardInfoService;
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cardinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<CardInfoEntity> cardInfoList = cardInfoService.queryListByWareNo(query);
        int total = cardInfoService.queryTotalByWareNo(query);

        PageUtils pageUtil = new PageUtils(cardInfoList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cardinfo:info")
    public R info(@PathVariable("id") Long id) {
        CardInfoEntity cardInfo = cardInfoService.queryObject(id);

        return R.ok().put("cardInfo", cardInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Long wareId, String pwds) {
        if(StringUtils.isEmpty(pwds)){
            return R.error("至少需要一个激活码");
        }
        CardInfoEntity cardInfo;
        Date date = getExpiryDate();
        List<CardInfoEntity> list = new ArrayList<>();
        Map map = new HashMap();

        String wareNo = null;
        if(wareId==null){
            return R.error("添加失败！");
        }
        //通过商品id查询商品
        WareInfoEntity wareInfoEntity = wareInfoService.queryObject(wareId);
        if (wareInfoEntity == null) {
            return R.error();
        }
        wareNo = wareInfoEntity.getWareNo();

        String[] pwdList = pwds.split(",");
        if(pwdList==null||pwdList.length==0){
            return  R.error("需要至少一个激活码");
        }

        for (int i = 0; i < pwdList.length; i++) {
            cardInfo = new CardInfoEntity();
            cardInfo.setWareNo(wareNo);
            cardInfo.setPassword(pwdList[i]);
            cardInfo.setExpiryDate(date);
            cardInfoService.save(cardInfo);
            list.add(cardInfo);
        }
        map.put("wareNo", wareNo);
        map.put("cardInfoList", list);
        return R.ok(map);
    }

    private Date getExpiryDate() {
        //卡密有效期，当前时间后推一年
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 1);
        return curr.getTime();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cardinfo:update")
    public R update(@RequestBody CardInfoEntity cardInfo) {
        cardInfoService.update(cardInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cardinfo:delete")
    public R delete(@RequestBody Long[] ids) {
        cardInfoService.deleteBatch(ids);

        return R.ok();
    }

    /**
     * 从excel导入数据
     *
     * @param file
     * @param wareId
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @RequestMapping("/importExcel")
    public R importExcel(@RequestParam("file") MultipartFile file, Long wareId) throws IOException, InvalidFormatException {
        CardInfoEntity cardInfoEntity = null;
        List<CardInfoEntity> list = new ArrayList<CardInfoEntity>();
        FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
        if (null != file) {
            list = readXls(fileInputStream);
        }

        String wareNo = null;
        if (wareId != null) {
            //通过商品id查询商品
            WareInfoEntity wareInfoEntity = wareInfoService.queryObject(wareId);
            if (wareInfoEntity == null) {
                return R.error();
            }
            wareNo = wareInfoEntity.getWareNo();
        }

        for (int i = 0; i < list.size(); i++) {
            cardInfoEntity = list.get(i);
            cardInfoEntity.setWareNo(wareNo);
            cardInfoEntity.setExpiryDate(getExpiryDate());
            cardInfoService.save(cardInfoEntity);
        }
        Map map = new HashMap();
        map.put("wareNo", wareNo);
        map.put("cardInfoList", list);
        return R.ok(map);
    }

    public List<CardInfoEntity> readXls(FileInputStream fileInputStream)
            throws IOException, InvalidFormatException {

        InputStream is = fileInputStream;
        List<CardInfoEntity> list = new ArrayList<CardInfoEntity>();
        if (is != null) {
            Workbook wb = WorkbookFactory.create(is);
            CardInfoEntity cardInfoEntity = null;

            // 循环工作表Sheet
            for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
                Sheet sheet = wb.getSheetAt(numSheet);
                if (sheet == null) {
                    continue;
                }
                // List<CardInfoEntity> colums = cardInfoService.queryColumns();
                // 循环行Row, 模板中目前只有两列，激活码和状态
                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row != null) {
                        cardInfoEntity = new CardInfoEntity();
                        Cell password = row.getCell(0);
                        cardInfoEntity.setPassword(getValue(password));
                        list.add(cardInfoEntity);
                        // 遍历列Cell
/*                        for (int cellNum = 0; cellNum <= hssfRow
                                .getLastCellNum(); cellNum++) {
                            HSSFCell hssfCell = hssfRow.getCell(cellNum);
                            if (hssfCell == null) {
                                continue;
                            }
                            String tableColum = colums.get(cellNum + 1).getStr(
                                    "COLUMN_NAME");
                            cardInfoEntity.set(tableColum, getValue(hssfCell));
                        }
                        list.add(company);*/
                    }
                }
            }
        }
        return list;
    }

    public static String getValue(Cell cell) {
        if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return String.valueOf(cell.getStringCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(cell.getStringCellValue());
        }
    }
}
