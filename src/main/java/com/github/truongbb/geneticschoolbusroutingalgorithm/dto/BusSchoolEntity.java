package com.github.truongbb.geneticschoolbusroutingalgorithm.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusSchoolEntity {

    List<Route> routes; // danh sách các tuyến xe
    int[][] chromosome; // nhiễm sắc thể phục vụ việc tối ưu tìm kiếm và đi lai ghép, đột biến
    boolean isElite; // thực thể này có phải là đáp án tối ưu, tốt nhất chưa

    /**
     * Cấu trúc của nhiễm sắc thể chromosome như sau:
     *      Mảng 2 chiều [n][m] với
     *          n = số điểm dừng (bus stop number)
     *          m = 2 (vị trí đầu tiên lưu id tuyến, vị trí thứ 2 lưu vị trí của điểm dừng trong tuyến đó)
     *
     *      ==> Mảng 2 chiều chromosome mang ý nghĩa lưu trữ xem điểm dừng đó nằm ở tuyến nào, vị trí nào trong tuyến
     *
     */

}
