DESCRIPTION = "NSS Dataplane"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"


FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-dp \
	   file://files \
	   "

DEPENDS:${SOC}:append += "virtual/kernel qca-ssdk-nohnat qca-nss-ppe"

DEPENDS:ipq807x:remove = "qca-nss-ppe"
DEPENDS:ipq807x_64:remove = "qca-nss-ppe"

S = "${WORKDIR}/qca-nss-dp"
EXTRA_CFLAGS += "-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-nss-ppe \
		"

MODULE_EXTRA_SYMBOLS = "${STAGING_INCDIR}/qca-ssdk/Module.symvers ${STAGING_INCDIR}/qca-nss-ppe/Module.symvers"

NSS_PPE_MODULES_${SOC} = " dp-ppe-ds=y"

NSS_PPE_MODULES_ipq807x:remove += "dp-ppe-ds=y"
NSS_PPE_MODULES_ipq807x_64:remove += "dp-ppe-ds=y"

PACKAGES += "kernel-module-qca-nss-dp"

do_configure() {
	true
}

do_compile:prepend() {
	rm -f ${S}/exports/nss_dp_arch.h
	lnr ${S}/hal/soc_ops/${SOC_TYPE}/nss_${SOC_TYPE}.h ${S}/exports/nss_dp_arch.h
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${NSS_PPE_MODULES} \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
		SoC="${SOC_TYPE}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-dp${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-dp
	install -m 0644 exports/* ${D}${includedir}/qca-nss-dp/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-dp/Module.symvers
	if [ "${SOC_TYPE}" != "ipq807x" ]; then
		install -d ${D}${bindir}
		install -m 0755 ${WORKDIR}/files/qca-nss-dp.init ${D}${bindir}/qca-nss-dp
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/files/qca-nss-dp.service ${D}${systemd_unitdir}/system/qca-nss-dp.service
	fi
}

FILES:${PN}_ipq95xx_64 =" \
	${bindir}/qca-nss-dp \
	${systemd_unitdir}/system/qca-nss-dp.service \
	"
FILES:${PN}_ipq95xx =" \
	${bindir}/qca-nss-dp \
	${systemd_unitdir}/system/qca-nss-dp.service \
	"
FILES:${PN}_ipq53xx_64 =" \
	${bindir}/qca-nss-dp \
	${systemd_unitdir}/system/qca-nss-dp.service \
	"
FILES:${PN}_ipq53xx =" \
	${bindir}/qca-nss-dp \
	${systemd_unitdir}/system/qca-nss-dp.service \
	"

SYSTEMD_SERVICE:${PN}_${SOC}:append += "qca-nss-dp.service"
FILES_${PN}-dev = "${includedir}/qca-nss-dp"
SYSTEMD_SERVICE:${PN}_ipq807x:remove += "qca-nss-dp.service"
SYSTEMD_SERVICE:${PN}_ipq807x_64:remove += "qca-nss-dp.service"

INSANE_SKIP:${PN} = "dev"
KERNEL_MODULE_AUTOLOAD += "qca-nss-dp"
