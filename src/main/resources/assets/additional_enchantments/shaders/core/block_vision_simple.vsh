#version 150

// All inputs need to be specified otherwise color (important part) will be incorrectly mapped
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float DepthBias; // view-space depth bias to mitigate z-fighting

out vec2 texCoord;
out vec4 vertColor;

void main() {
    texCoord = UV0;
    vertColor = Color;
    // Compute view-space position and nudge slightly along view direction to avoid coplanar z-fighting
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    vec3 viewDir = normalize(-viewPos.xyz);
    viewPos.xyz += viewDir * DepthBias;

    gl_Position = ProjMat * viewPos;
}
