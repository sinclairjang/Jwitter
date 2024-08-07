package org.zerobase.jwitter.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerobase.jwitter.domain.model.Follow;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("Follow information")
public class FollowDto implements Serializable {

    @NotNull
    @ApiModelProperty(value = "follower information", required = true)
    private UserDto.Out follower;

    @NotNull
    @ApiModelProperty(value = "followee information", required = true)
    private UserDto.Out2 followee;

    public static FollowDto from(Follow followInfo) {
        return FollowDto.builder()
                .follower(UserDto.Out.from(followInfo.getFollower()))
                .followee(UserDto.Out2.from(followInfo.getFollowee()))
                .build();
    }
}
