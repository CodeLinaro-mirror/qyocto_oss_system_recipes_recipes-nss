DESCRIPTION = "NSS Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-drv \
	   file://files \
	   "

DEPENDS = "virtual/kernel qca-nss-dp"
RDEPENDS:${PN} += "qca-nss-dp"

S = "${WORKDIR}/qca-nss-drv"

PACKAGES += "kernel-module-qca-nss-drv"
INSANE_SKIP:${PN} = "dev"

NSS_CLIENTS_DIR = "${TOPDIR}/../opensource/qca-nss-clients/exports"

do_configure() {
	true
}

do_compile:prepend() {
	rm -f ${S}/exports/nss_arch.h
	lnr ${S}/exports/arch/nss_${SOC_TYPE}.h ${S}/exports/nss_arch.h
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="-I${STAGING_INCDIR}/qca-nss-dp -I${STAGING_INCDIR}/qca-ssdk/" \
		KBUILD_EXTRA_SYMBOLS="${STAGING_INCDIR}/qca-nss-dp/Module.symvers ${STAGING_INCDIR}/qca-ssdk-nohnat/Module.symvers" \
		SoC="${SOC_TYPE}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-drv${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-drv
	cp -r ${WORKDIR}/qca-nss-drv/exports/* ${D}${includedir}/qca-nss-drv/

	rm -rf ${D}${includedir}/qca-nss-drv/nss_ipsecmgr.h
	rm -rf ${STAGING_INCDIR}/qca-nss-clients/nss_ipsecmgr.h
	install -d ${D}${includedir}/qca-nss-clients
	install -m 0644 ${NSS_CLIENTS_DIR}/nss_ipsecmgr.h ${D}${includedir}/qca-nss-clients/.

	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/qca-nss-drv.init ${D}${bindir}/qca-nss-drv
	install -m 0755 ${WORKDIR}/files/qca-nss-drv-hotplug.sh ${D}${bindir}/qca-nss-drv-hotplug
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/files/qca-nss-drv.service ${D}${systemd_unitdir}/system/qca-nss-drv.service
	install -m 0644 ${WORKDIR}/files/qca-nss-drv-hotplug.path ${D}${systemd_unitdir}/system/qca-nss-drv-hotplug.path
	install -m 0644 ${WORKDIR}/files/qca-nss-drv-hotplug.service ${D}${systemd_unitdir}/system/qca-nss-drv-hotplug.service
	install -d ${D}${sysconfdir}/sysctl.d
	install -m 0644 ${WORKDIR}/files/qca-nss-drv.sysctl ${D}${sysconfdir}/sysctl.d/99-qca-nss-drv.conf
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-drv/Module.symvers
}

SYSTEMD_SERVICE:${PN} += "qca-nss-drv.service qca-nss-drv-hotplug.path qca-nss-drv-hotplug.service"

FILES:${PN} = " ${bindir}/qca-nss-drv \
	${includedir}/qca-nss-clients/* \
	${includedir}/qca-nss-drv/* \
	${bindir}/qca-nss-drv-hotplug \
	${systemd_unitdir}/system/qca-nss-drv.service \
	${systemd_unitdir}/system/qca-nss-drv-hotplug.path \
	${systemd_unitdir}/system/qca-nss-drv-hotplug.service \
	${sysconfdir}/sysctl.d/99-qca-nss-drv.conf \
	"

FILES:${PN}-dev = "{includedir}/*"
INSANE_SKIP:${PN} = "dev"
KERNEL_MODULE_AUTOLOAD += "qca-nss-drv"
module_autoload:${PN} = "qca-nss-dp qca-nss-drv"
