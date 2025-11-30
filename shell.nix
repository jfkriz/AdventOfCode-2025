let pkgs = import <nixpkgs> { };

in pkgs.mkShell {
  buildInputs = with pkgs; [
    gradle
    kotlin
    jdk
  ];
}

