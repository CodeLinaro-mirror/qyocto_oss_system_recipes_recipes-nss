SUMMARY = "QCA NSS Firmware Package"
LICENSE = "CLOSED"

inherit module

PKG_NAME = "qca-nss-fw-hk"
PKG_BRANCH = "nss"
PKG_VERSION = "156"
PKG_RELEASE = "1"

NSS_VER = "12.2"
NSS_SOC = "HK"
NSS_PROFILE = "R"

PKG_SOURCE = "BIN-NSS.FW.${NSS_VER}-${PKG_VERSION}-${NSS_SOC}.${NSS_PROFILE}"
PKG_SOURCE_URL = "http://qcawebsrvr.qualcomm.com/NSS/NSS.FW/${NSS_VER}/${NSS_SOC}/${PKG_VERSION}/"

SRC_URI += "${PKG_SOURCE_URL}${PKG_SOURCE}.tar.bz2;name=${PKG_NAME} \
	   "

BB_STRICT_CHECKSUM = "0"
#SRC_URI[qca-nss-fw-hk.md5sum] = "1be4026a554551722b92fae3cc96068f"
#SRC_URI[qca-nss-fw-hk.sha256sum] = "32c882ec62408ed72cedee89db66206bd18416565d25184e2c6016e3f90f11dd"

S = "${WORKDIR}/${PKG_SOURCE}"

do_unpack() {
	tar -xvjf ${DL_DIR}/${PKG_SOURCE}.tar.bz2 -C ${WORKDIR}/
}

do_compile() {
}

do_install() {
	install -d ${D}${base_libdir}/firmware/qca
	install -m 0755 ${S}/retail_router0.bin ${D}${base_libdir}/firmware/qca-nss0-retail.bin
	install -m 0755 ${S}/retail_router1.bin ${D}${base_libdir}/firmware/qca-nss1-retail.bin
	install -m 0755 ${S}/retail_router0.bin ${D}${base_libdir}/firmware/qca-nss0.bin
	install -m 0755 ${S}/retail_router1.bin ${D}${base_libdir}/firmware/qca-nss1.bin
}

FILES_${PN} = " \
	${base_libdir}/firmware/qca \
	${base_libdir}/firmware/qca-nss0-retail.bin \
	${base_libdir}/firmware/qca-nss1-retail.bin \
	${base_libdir}/firmware/qca-nss0.bin \
	${base_libdir}/firmware/qca-nss1.bin \
"
