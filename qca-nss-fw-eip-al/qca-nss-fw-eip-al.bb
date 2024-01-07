SUMMARY = "QTI NSS EIP Firmware Package"
LICENSE = "CLOSED"

inherit module

PKG_NAME = "qca-nss-fw-eip"
PKG_BRANCH = "nss"
PKG_VERSION = "3.3"
PKG_RELEASE = "1"
PKG_MD5SUM = "skip"

EIP197_VER = "3.3"

PKG_SOURCE ="BIN-EIP197.AL.${EIP197_VER}"
PKG_SOURCE_URL = "http://vm-cnsswebserv/NSS/NSS.FW/EIP197.AL/${EIP197_VER}"

SRC_URI += "${PKG_SOURCE_URL}/${PKG_SOURCE}.tar.bz2 \
	   "

BB_STRICT_CHECKSUM = "0"

S = "${WORKDIR}/${PKG_SOURCE}"

do_unpack() {
	tar -xvjf ${DL_DIR}/${PKG_SOURCE}.tar.bz2 -C ${WORKDIR}/
}

do_compile() {
}

do_install() {
	install -d ${D}${base_libdir}/firmware/
	install -m 0644 ${S}/ifpp.bin ${D}${base_libdir}/firmware/ifpp.bin
	install -m 0644 ${S}/ipue.bin ${D}${base_libdir}/firmware/ipue.bin
	install -m 0644 ${S}/ofpp.bin ${D}${base_libdir}/firmware/ofpp.bin
	install -m 0644 ${S}/opue.bin ${D}${base_libdir}/firmware/opue.bin
}

FILES:${PN} = " \
	${base_libdir}/firmware \
	${base_libdir}/firmware/ifpp.bin \
	${base_libdir}/firmware/ipue.bin \
	${base_libdir}/firmware/ofpp.bin \
	${base_libdir}/firmware/opue.bin \
"
