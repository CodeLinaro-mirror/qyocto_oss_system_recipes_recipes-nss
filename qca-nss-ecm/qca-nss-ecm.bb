DESCRIPTION = "NSS ECM (Enhanced Connection Manager)"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-ecm \
	   file://files \
	   "

DEPENDS = "virtual/kernel simulated-driver"

S = "${WORKDIR}/qca-nss-ecm"

PACKAGES += "kernel-module-ecm"
INSANE_SKIP_${PN} = "dev"

export ECM_FRONT_END_NSS_ENABLE="n"

do_compile() {
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="arm" \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="-I${STAGING_INCDIR}/shortcut-fe" \
		SoC="ipq40xx" \
		modules
}
do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ecm${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/files/ecm_dump.sh ${D}${bindir}/ecm_dump.sh
	install -m 0755 ${WORKDIR}/files/qca-nss-ecm  ${D}${bindir}/qca-nss-ecm
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/files/qca-nss-ecm.service ${D}${systemd_unitdir}/system/qca-nss-ecm.service
	install -d ${D}${sysconfdir}/sysctl.d
	install -m 0644 ${WORKDIR}/files/qca-nss-ecm.sysctl ${D}${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf
}

SYSTEMD_SERVICE_${PN} += "qca-nss-ecm.service"

FILES_${PN} = " \
	${systemd_unitdir}/system/qca-nss-ecm.service \
	${bindir}/qca-nss-ecm \
	${bindir}/ecm_dump.sh \
	${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf \
"
