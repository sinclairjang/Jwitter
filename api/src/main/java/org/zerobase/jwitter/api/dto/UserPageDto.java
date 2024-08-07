package org.zerobase.jwitter.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel("User page response")
public class UserPageDto implements Serializable  {
    @ApiModelProperty(name = "Content")
    List<UserDto.Out> content;

    @ApiModelProperty(name = "Total Page", example = "4")
    int totalPage;

    @ApiModelProperty(name = "Total Elements", example = "20")
    long totalElements;

    @ApiModelProperty(name = "Current Page", example = "1")
    int currentPage;

    @ApiModelProperty(name = "Page Size", example = "5")
    int pageSize;
}
