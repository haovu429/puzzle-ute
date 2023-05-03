package hcmute.puzzle.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudinaryUploadFileResponse {
    String signature;
    String format;
    String resource_type;
    String secure_url;
    String created_at;
    String asset_id;
    String version_id;
    String type;
    int version;
    String url;
    String public_id;
    ArrayList<String> tags;
    String folder;
    String api_key;
    int bytes;
    int width;
    String etag;
    boolean placeholder;
    int height;
}
